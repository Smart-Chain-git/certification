[Index](../README.md) | [Architecture](../documentation/architecture.md) | [Smart contract](./README.md) | [Docker Deployment](../documentation/docker-deployment.md) | [Quick Start](../documentation/quickstart.md)

# Smart contract

## Requirements

* LIGO
* Tezos client
* Access to a tezos node

### Install ligo

```
curl https://gitlab.com/ligolang/ligo/raw/dev/scripts/installer.sh | bash -s "next"
```
[source](https://ligolang.org/docs/next/intro/installation/)

## Compile and test with LIGO

* Compile contract
```
ligo compile-contract src/hash_timestamping.mligo addHash
```

* Compile empty storage
```
ligo compile-storage src/hash_timestamping.mligo addHash "(Big_map.empty : storage)"
```

* Test with empty storage
```
ligo dry-run src/hash_timestamping.mligo addHash '"9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08"' "(Big_map.empty : storage)"
```

## Add contract to tezos blockchain

* Update the client config in the Makefile (-A for the node address and -P for the port)
* To update the client config, run:
```
make updateClient
```
* Update the account adding the contract to the blockchain by updating the signer in the Makefile
* To add the contract to the blockchain, run:
```
make originate
```
