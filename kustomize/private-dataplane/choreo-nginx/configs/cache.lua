-- --------------------------------------------------------------------
-- Copyright (c) 2022, WSO2 Inc. (http://wso2.com) All Rights Reserved.
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- -----------------------------------------------------------------------

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
