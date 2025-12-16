FROM python:3.11-slim

WORKDIR /app

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    PORT=8082

# install build deps needed for some wheels (kept minimal)
RUN apt-get update && \
    apt-get install -y --no-install-recommends build-essential ca-certificates && \
    rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# create non-root user and copy app
RUN useradd --create-home appuser
COPY . .
RUN chown -R appuser:appuser /app
USER appuser

EXPOSE 8082

# Production: gunicorn with sync workers for Flask
CMD ["gunicorn", "--bind", "0.0.0.0:8082", "--workers", "2", "main:app"]