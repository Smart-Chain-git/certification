# Question and Answer

* **What is channel management**?  
You can create a token with the channel management feature. This token is required when you want to use the web API (see [Swagger UI](/swagger-ui.html)).
  
* **Are my files uploaded on the Digisign Server**?    
No. the files you upload in the Signature Request or the signature Check pages are not sent to the Digisign server. the files are only uploaded in your Web Browser to compute the hash codes. Only the hash codes are sent to the Digisign server and hence, there is no confidentiality issue.  
  
* **How does the Digisign Server know the signer entities**?  
The Digisign server has a database with some couples public key/entities. If the hash of the document you're checking is stored in the database, Digisign can provide the name of the signer. Otherwise, it can only provide the public key of the signer (which is in the proof file related to the document).