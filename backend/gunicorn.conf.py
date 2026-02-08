"""Gunicorn configuration for SIGESI."""

bind = "0.0.0.0:8000"
workers = 4
worker_class = "gthread"
threads = 2
timeout = 120
graceful_timeout = 30
keepalive = 5
max_requests = 1000
max_requests_jitter = 50
accesslog = "-"
errorlog = "-"
loglevel = "info"
proc_name = "sigesi"
