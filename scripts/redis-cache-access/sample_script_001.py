import redis
import base64
import json
from azure.identity import DefaultAzureCredential

scope = "https://redis.azure.com/.default"  # The current scope is for public preview and may change for GA release.
host = "choreo-dev-rate-limit.redis.cache.windows.net"  # Required
port = 6380  # Required

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

def get_keys_count():
    cred = DefaultAzureCredential()
    token = cred.get_token(scope)
    user_name = extract_username_from_token(token.token)
    
    r = redis.Redis(host=host,
                    port=port,
                    ssl=True,    # ssl connection is required.
                    username=user_name,
                    password=token.token,
                    decode_responses=True)
    key_count = r.dbsize()
    print("Total Keys:", key_count)

if __name__ == '__main__':
    get_keys_count()
