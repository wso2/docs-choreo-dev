import time
import hmac
import hashlib
import base64
from urllib.parse import quote_plus, urlencode

def _sign_string(uri, key, key_name):
    expiry = int(time.time() + 100000000000)

    string_to_sign = quote_plus(uri) + '\n' + str(expiry)

    key = key.encode('utf-8')
    string_to_sign = string_to_sign.encode('utf-8')
    signed_hmac_sha256 = hmac.HMAC(key, string_to_sign, hashlib.sha256)
    signature = signed_hmac_sha256.digest()
    signature = base64.b64encode(signature)

    return 'SharedAccessSignature sr=' + quote_plus(uri)  + '&sig=' + quote_plus(signature) + '&se=' + str(expiry) + '&skn=' + key_name


if __name__ == '__main__':    
    URI = "<Topic URI>"  # [sample] - choreo-dev-servicebus.servicebus.windows.net/billingcycleresetevent
    POLICY = "<Policy>"  # [sample] - choreo-dev-servicebus-rule1
    POLICY_KEY = "<Policy Key>" # [sample] - policy key secret

    print(_sign_string(URI,POLICY_KEY,POLICY))
