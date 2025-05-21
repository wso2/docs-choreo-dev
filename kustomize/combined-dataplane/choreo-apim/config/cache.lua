local cache = {}

local lrucache = require "resty.lrucache"
local maximumCacheValue = 10000

-- allow up to 10000 items in the cache
local localCache, err = lrucache.new(maximumCacheValue)
if not localCache then
    error("failed to create the cache: " .. (err or "unknown"))
end

-- Returns NGINX local cache
function cache.getCache()
    return localCache
end

return cache
