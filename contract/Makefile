LIGO := ligo
CLIENT := ../scripts/bin/tezos-client
CLIENT_CONFIG := -A 127.0.0.1 -P 18731
SIGNER = alice

compile:
	($(LIGO) compile-contract hash_timestamping.mligo addHash) > hash_timestamping.tz
	sed -i.bak 's/\r$$//' hash_timestamping.tz
	rm *.bak

clean:
	rm -f *.tz *.pp.ligo *.bak

originate: compile
	$(CLIENT) originate contract hash_timestamping transferring 0 \
		from $(SIGNER) running hash_timestamping.tz \
		--init '{}' --burn-cap 5 --force

updateClient:
	$(CLIENT) $(CLIENT_CONFIG) config update
