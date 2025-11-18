CREATE TABLE users (
  id TEXT PRIMARY KEY,
  name TEXT NOT NULL,
  email TEXT,
  phone TEXT,
  subscribed TEXT[] NOT NULL,  -- array of categories
  channels TEXT[] NOT NULL
);

CREATE TABLE messages (
  id SERIAL PRIMARY KEY,
  category TEXT NOT NULL,
  body TEXT NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE delivery_logs (
  id SERIAL PRIMARY KEY,
  message_id INTEGER REFERENCES messages(id) ON DELETE CASCADE,
  user_id TEXT REFERENCES users(id) ON DELETE CASCADE,
  channel TEXT NOT NULL,
  ok BOOLEAN NOT NULL,
  detail TEXT,
  timestamp TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_delivery_logs_timestamp ON delivery_logs (timestamp DESC);
CREATE INDEX idx_users_subscribed ON users USING GIN (subscribed);
