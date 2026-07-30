-- KEYS[1] stockKey: seckill:stock:{voucherId}
-- KEYS[2] orderKey: seckill:order:{voucherId}
-- ARGV[1] userId
local stockKey = KEYS[1]
local orderKey = KEYS[2]
local userId = ARGV[1]
local stock = tonumber(redis.call('GET', stockKey))
if stock == nil or stock <= 0 then
  return 1
end
if redis.call('SISMEMBER', orderKey, userId) == 1 then
  return 2
end
redis.call('INCRBY', stockKey, -1)
redis.call('SADD', orderKey, userId)
return 0
