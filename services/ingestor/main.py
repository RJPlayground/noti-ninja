from datetime import datetime
import json
import logging
import os
from typing import Dict, Optional

from flask import Flask, request, jsonify, abort
from pydantic import BaseModel, Field, ValidationError

app = Flask(__name__)

logger = logging.getLogger("ingestor")
handler = logging.StreamHandler()
formatter = logging.Formatter('%(asctime)s %(levelname)s %(name)s %(message)s', "%Y-%m-%dT%H:%M:%S%z")
handler.setFormatter(formatter)
logger.addHandler(handler)
logger.setLevel(logging.INFO)


class Notification(BaseModel):
    id: str = Field(..., description="Unique notification id")
    source: Optional[str] = Field(None, description="Source system")
    type: Optional[str] = Field(None, description="Notification type")
    timestamp: Optional[datetime] = Field(None, description="Event timestamp")
    payload: Dict = Field(default_factory=dict, description="Arbitrary payload")


@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok"})


@app.route("/ingest", methods=["POST"])
def ingest():
    try:
        body = request.get_json(force=True)
        notif = Notification.model_validate(body)
        record = notif.model_dump()
        if record.get("timestamp") and isinstance(record["timestamp"], datetime):
            record["timestamp"] = record["timestamp"].isoformat()
        logger.info("ingested_notification %s", json.dumps(record, ensure_ascii=False))
        return jsonify({"status": "accepted", "id": notif.id}), 202
    except ValidationError as ve:
        logger.warning("validation_failed: %s", ve)
        return jsonify({"detail": ve.errors()}), 422
    except Exception:
        logger.exception("failed_to_ingest")
        abort(500, "ingest failed")


if __name__ == "__main__":
    port = int(os.environ.get("PORT", "8082"))
    # For dev only; production use gunicorn
    app.run(host="0.0.0.0", port=port)