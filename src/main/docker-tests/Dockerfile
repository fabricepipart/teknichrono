FROM python:3.6-slim

COPY src/test/scripts /app
COPY requirements.txt /app/requirements.txt
WORKDIR /app
RUN pip install -r /app/requirements.txt
CMD ["sh", "/app/bash/all_tests.sh"]
