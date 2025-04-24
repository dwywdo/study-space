---@diagnostic disable: undefined-global

local counter_ip_key = KEYS[1]
local block_period_seconds = tonumber(ARGV[1])
local current_timestamp_milliseconds = tonumber(ARGV[2])
local current_request_seq_id = tonumber(ARGV[3])
local request_limit = tonumber(ARGV[4])
local window_size = tonumber(ARGV[5])

redis.log(redis.LOG_NOTICE, "==========================================================")

local zscore_for_blocked = redis.call('ZSCORE', counter_ip_key, 'blocked')
local is_blocked = (tonumber(zscore_for_blocked) or 0) == 1

if block_period_seconds > 0 and is_blocked then
    redis.log(redis.LOG_NOTICE, "[BLOCKED] ")
    return {1, redis.call('TTL', counter_ip_key)}
end

local countInWindow = redis.call('ZCOUNT', counter_ip_key, tonumber(current_timestamp_milliseconds) - tonumber(window_size) * 1000, current_timestamp_milliseconds)

redis.log(redis.LOG_NOTICE, "request_limit: " .. tostring(request_limit))
redis.log(redis.LOG_NOTICE, "countInWindow: " .. tostring(countInWindow))

redis.call('ZADD', counter_ip_key, current_timestamp_milliseconds, current_request_seq_id)
redis.call('ZREMRANGEBYRANK', counter_ip_key, 0, (-1 * request_limit -1))
redis.call('EXPIRE', counter_ip_key, window_size)

if (countInWindow + 1) >= request_limit then
    redis.call('ZADD', counter_ip_key, '1', 'blocked')
    redis.call('EXPIRE', counter_ip_key, block_period_seconds)
end

redis.log(redis.LOG_NOTICE, "[ALLOWED]")
return {0, 0}
