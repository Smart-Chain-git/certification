import sys
import base58
from binascii import hexlify

spkPrefix = b'\x2b\xf6\x4e\x07'

def compute_spk(argv):
    skB58 = argv[0]
    pkB58 = argv[1]
    print("Base58 secret key: ", skB58)
    print("Base58 public key: ", pkB58)
    sk = extract_key_from_b58(skB58)
    pk = extract_key_from_b58(pkB58)
    spk = spkPrefix + sk + pk
    # print("Built secret public key with prefix: ", hexString(spk))
    spkB58 = base58.b58encode_check(spk)
    print("Base58 secret public key: ", byteString(spkB58))

def extract_key_from_b58(b58):
    key = base58.b58decode_check(b58)[4:] # Remove prefix of 4 bytes
    # print("Extracted key: ", hexString(key))
    return key
    
# Display functions
def hexString(bytes):
    return byteString(hexlify(bytes))

def byteString(bytes):
    return str(bytes)[2:-1]

# Main
if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: $0 tz_secret_key tz_public_key")
        sys.exit(0)
    compute_spk(sys.argv[1:])