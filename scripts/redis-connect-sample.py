import redis
import base64
import json
from azure.identity import DefaultAzureCredential

host = "choreo-dev-wu2-rate-limit.redis.cache.windows.net"  # Set the Azure Redis Cache host name
key_pattern = '*key*' # Set the key pattern

scope = "https://redis.azure.com/.default"
port = 6380

def extract_username_from_token(token):
    parts = token.split('.')
    base64_str = parts[1]

    if len(base64_str) % 4 == 2:
        base64_str += "=="
    elif len(base64_str) % 4 == 3:
        base64_str += "="

    json_bytes = base64.b64decode(base64_str)
    json_str = json_bytes.decode('utf-8')
    jwt = json.loads(json_str)

    return jwt['oid']

def hello_world():
    cred = DefaultAzureCredential()
    token = cred.get_token(scope)
    user_name = extract_username_from_token(token.token)
    r = redis.Redis(host=host,
                    port=port,
                    ssl=True,
                    username=user_name,
                    password=token.token,
                    decode_responses=True)

    # Use the SCAN command to iterate over keys matching the pattern
    cursor = 0
    keys = []
    while True:
        cursor, matched_keys = r.scan(cursor=cursor, match=key_pattern)
        keys.extend(matched_keys)
        if cursor == 0:
            break

    # Retrieve the values for the matched keys
    for key in keys:
        value = r.get(key)
        print(f"Key: {key}, Value: {value}")

if __name__ == '__main__':
    hello_world()
