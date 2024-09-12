local split = {}

-- Returns an array with split values
--
-- @param string string value
-- @param delimiter string split charactor
-- @return Array with split values
function split.splitString(string, delimiter)
    local result = {};
    for match in (string..delimiter):gmatch("(.-)"..delimiter) do
        table.insert(result, match);
    end
    return result;
end

return split
