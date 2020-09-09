# Question and Answer

* **What is channel management?**
You can create a token with the channel management feature. This token is required when you want to use the web API (see [Swagger UI](/swagger-ui.html)).
  
* **Are my files uploaded on the Digisign Server?**   
No. the files you upload in the Signature Request or the signature Check pages are not sent to the Digisign server. the files are only uploaded in your Web Browser to compute the hash codes. Only the hash codes are sent to the Digisign server and hence, there is no confidentiality issue.  
  
* **How does the Digisign Server know the signer entities?**  
The Digisign server has a database with some couples public key/entities. If the hash of the document you're checking is stored in the database, Digisign can provide the name of the signer. Otherwise, it can only provide the public key of the signer (which is in the proof file related to the document).
  
* **Can I use this website for my business?**
No, this website as it is is just a demonstration tool of Tezos DigiSign. If you want to use it for your business, you will need to customize it, modify its content (eg: this Q&A section). We also advise you not to use this GUI for users interaction, but better to integrate your existing web pages /app with the Tezos DigiSign server using its API.

* **How much does it cost to use Tezos DigiSign?**
Tezos DigiSign is free of charge. However, to benefit from the Tezos Blockchain, you will need to pay small transactions fees in tez, the Tezos native coins. These fees may vary in time, at the moment we launch this service they are close to 20 cents per transaction. Since we implemented a massification engine , based on a Merkle tree approach, you can sign thousands of document through a single transaction.
This demonstration website is not connected to the Tezos MainNet, but to its testnet Carthagenet which does not take fees.

* **What is the legal value of a document signed using Tezos DigiSign?**
We cannot provide a unique answer, it depends on your geography, and the type of documents you are signing. French speaking users can find more information on the French context in the whitepaper in the linked below. If you implement it correctly, we believe Tezos DigiSign is compliant with the “simple” digital signature as defined in the eIDAS regulation.
https://www.blockchain-ez.com/wp-content/uploads/2020/06/Livre-blanc-Analyse-de-la-signature-électronique-sur-blockchain-Solegal-Blockchain-EZ.pdf

* **What is the product roadmap?**
As an open source project, the product roadmap depends…on you!
The CORE TEAM has a lot of ideas, but not all of them can be implemented without financial support. Please contact it for more information on the roadmap. TEZOSDIGISIGN@sword-group.com
