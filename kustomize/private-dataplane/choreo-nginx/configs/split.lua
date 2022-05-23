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
