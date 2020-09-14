# Creation of users

The create_account_cathagenet.sh scripts enables to create conveniently one user account.

## Requirements

* Tezos client installed (see install_tezos_client.sh script)
* An admin user created in the Tezos Digisign with a token


## Creation

Go to the Tezos Faucet [https://faucet.tzalpha.net/](https://faucet.tzalpha.net/)  
  
Retrieve the json file and put it on your server  
  
Launch the script 
`create_account_cathagenet.sh <url (url of the Tezos Digisign server)> <token> <account> <handle (name of the json file)> <fullname> <password>`  
The password muste be 10 characters length and contains one uppercase letter, one lowercase letter, one number and one special character.
