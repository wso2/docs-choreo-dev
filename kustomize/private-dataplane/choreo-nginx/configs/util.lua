local util = {}

-- Returns an array contains local cache value and search keys
--
-- @param organizationId organization UUID
-- @param uri request uri
-- @environment environment vhost
-- @return Array with search keys and cache value as the last element
function util.getLocalCacheValue(organizationId, uri, environment)
    local cacheValue
    local result = {}
    local local_adapter_label = "nil"
    local split = require "util.split"
    local cacheModule = require "util.cache"

    local splitUriArray = split.splitString(uri, "/")
    local cache = cacheModule.getCache()
    local keyPrefix = "#global-adapter#" .. environment .. "#" .. organizationId .. "#"

    while( #splitUriArray > 1 ) do
        -- Remove empty or null values at the end of array.
        if (splitUriArray[#splitUriArray] == nil and splitUriArray[#splitUriArray] == '') then
            table.remove(splitUriArray, #splitUriArray)
        end

        local key = keyPrefix .. table.concat(splitUriArray, "/")
        cacheValue = cache:get(key)
        table.insert(result, key);

        -- Set local_adapter_label if a value found in the local cache
        if cacheValue ~= nil then
            local_adapter_label = cacheValue
            break
        else
            --check number of strings left in the uri array
            if #splitUriArray == 1 then
                break
            end
            table.remove(splitUriArray,#splitUriArray)
        end
    end
    table.insert(result, local_adapter_label);
    return result;
end


-- Returns an array contains local cache value and search keys
--
-- @param organization organization name
-- @param uri request uri
-- @environment environment vhost
-- @return Array with search keys and cache value as the last element
function util.getOrgNameLocalCacheValue(organization, uri, environment)
    local cacheValue
    local result = {}
    local local_adapter_label = "nil"
    local split = require "util.split"
    local cacheModule = require "util.cache"

    local splitUriArray = split.splitString(uri, "/")
    local cache = cacheModule.getCache()
    local keyPrefix = "#global-adapter#" .. environment .. "#/" .. organization

    while( #splitUriArray > 1 ) do
        -- Remove empty or null values at the end of array.
        if (splitUriArray[#splitUriArray] == nil and splitUriArray[#splitUriArray] == '') then
            table.remove(splitUriArray, #splitUriArray)
        end

        local key = keyPrefix .. table.concat(splitUriArray, "/")
        cacheValue = cache:get(key)
        table.insert(result, key);

        -- Set local_adapter_label if a value found in the local cache
        if cacheValue ~= nil then
            local_adapter_label = cacheValue
            break
        else
            --check number of strings left in the uri array
            if #splitUriArray == 1 then
                break
            end
            table.remove(splitUriArray,#splitUriArray)
        end
    end
    table.insert(result, local_adapter_label);
    return result;
end

-- Returns array containing Redis MGET keys
--
-- @param keyArray redis search keys
-- @return Array Redis MGET keys
function util.getRedisKeyString(keyArray)
    local keyString = ""
    local cjson = require "cjson"

    for index, value in pairs(keyArray) do
        if (value ~= nil and value ~= '' and keyString == "") then
            keyString = "[\"" .. value .."\""
        elseif (value ~= nil and value ~= '') then
            keyString = keyString .. ", \"" .. value .."\""
        end
    end
    keyString = keyString .. "]"

    ngx.log(ngx.DEBUG, "keyString: ", keyString)
    return cjson.decode(keyString)
end

-- Returns array containing LA label and the cache key
--
-- @param valueArray redis response values
-- @return Array
function util.getRedisLocalAdapterLabel(valueArray)
    local index = #valueArray
    local result = {}
    local cjson = require "cjson"
    ngx.log(ngx.DEBUG, "valueArray: ", cjson.encode(valueArray))

    for k, value in pairs(valueArray) do
        -- Get last value of the array
        local label = valueArray[#valueArray - k + 1]
        if (label ~= nil and type(label) == "string") then
            table.insert(result, label);
            break
        else
            index = index - 1
        end
    end
    if (#result == 0) then
        ngx.log(ngx.DEBUG, "No values found in Redis")
        table.insert(result, "nil");
    end
    table.insert(result, index);
    return result
end

-- Returns LA k8s service name
--
-- @param localAdapterLabel label returned from the cache or redis DB
-- @return String LA k8s service name
function util.getLocalAdapterK8sServiceName(localAdapterLabel)
    local serviceName
    local split = require "util.split"

    if localAdapterLabel ~= nil then
        local splitLabelArray = split.splitString(localAdapterLabel, '/')
        -- Get the first element of the array
        serviceName = table.remove(splitLabelArray,1)
        ngx.log(ngx.DEBUG, "serviceName: ", serviceName)
    end
    return serviceName
end

-- Returns organization name from context
--
-- @param cacheValue label returned from the cache or redis DB
-- @return String organization name
function util.getOrganizationName(cacheValue)
    local organizationName
    local split = require "util.split"

    if cacheValue ~= nil then
        local splitLabelArray = split.splitString(cacheValue, '/')
        -- Get the first element of the array
        organizationName = table.remove(splitLabelArray,2)
        ngx.log(ngx.DEBUG, "organizationName: ", organizationName)
    end
    return organizationName
end

-- Returns userEnv
--
-- @param orgid-userenv string
-- @return user environment
function util.getUserEnv(orgdetails)
    local userEnv
    local split = require "util.split"

    if orgdetails ~= nil then
        local splitOrgDetails = split.splitString(orgdetails, '-')
        -- Get the last element of the array
        if #splitOrgDetails > 5 then
          userEnv = splitOrgDetails[#splitOrgDetails]
          ngx.log(ngx.DEBUG, "userEnv: ", userEnv)
        end
    end
    return userEnv
end

-- Returns organizationId
--
-- @param orgid-userenv string
-- @return Organization UUID
function util.getOrganizationId(orgdetails)
    local organizationId

    if orgdetails ~= nil then
        organizationId = string.gsub(orgdetails,"(.*)-.*$","%1")
        ngx.log(ngx.DEBUG, "Organization Id: ", organizationId)
    end
    return organizationId
end

return util
