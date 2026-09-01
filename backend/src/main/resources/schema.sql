CREATE TABLE IF NOT EXISTS call_monitoring (
    id               BIGSERIAL PRIMARY KEY,
    call_id          VARCHAR(50)   NOT NULL,
    call_timestamp   TIMESTAMP     NOT NULL,
    cs_name          VARCHAR(100)  NOT NULL,
    customer_name    VARCHAR(100)  NOT NULL,
    sentiment_score  NUMERIC(5,2)  NOT NULL CHECK (sentiment_score BETWEEN 0 AND 100)
);

CREATE INDEX IF NOT EXISTS idx_call_monitoring_call_timestamp ON call_monitoring (call_timestamp);
CREATE INDEX IF NOT EXISTS idx_call_monitoring_sentiment_score ON call_monitoring (sentiment_score);
