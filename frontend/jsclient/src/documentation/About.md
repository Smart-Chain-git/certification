<style type="text/css">

h2 {
	margin-top: 20px;
	margin-bottom: 10px;
}

h3 {
	margin-top: 8px;
	margin-bottom: 2px;
}


table {
  border-collapse: collapse;
}

table th {
  border: 1px solid black;
}

table td {
  border: 1px solid black;
  padding: 2px
}
</style>

# About

## Introduction

The development of information technology, smartphones and internet has greatly accelerated the development of electronic signature, the market (sale of services, software and hardware) has multiplied by almost four over the last five years to exceed in 2020 the three billion dollars. This strong growth seems to want to continue for a few more years with projections reaching 6 billion dollars in 2026.
The electronic signature aims to certify the authenticity of a document, and possibly provide proof of consent by a third party.
  
Driven by the market, regulations had to adapt to provide a relatively clear regulatory framework from 2014 with the arrival of European eIDAS regulation. This regulation allows different types of technical solutions. For various reasons, the market has so far been built essentially around solutions based on the issuance of certificates (PKI) and the use of trusted third parties (Certificate Authorities).
  
However, the use of these trusted third parties presents many constraints, and in particular high costs, for documents requiring only a simple signature which is by far the most widely used electronic signature with regard to market needs. Indeed, the cost of electronic signature solutions based on PKI constrains companies in their cost / benefit analyzes to favor only documents with high value or those subject to a regulatory obligation.
  
The use of blockchain technology today provides an extremely attractive alternative to using trusted third-party providers. It makes it possible to replace these third parties to benefit from timestamping and signing services, storage of proof and history of actions performed to verify the identity of the original issuer and any co-signers of a document.  From a technical point of view, the electronic signature of a document consists in signing via a cryptographic system with public keys the hash of a document resulting from the application of a hash function on this document. Blockchain technology natively has these cryptographic mechanisms and uses them to guarantee the integrity and authenticity of transactions integrated into its network. Thus, this technology is perfectly suited to be used for electronically signing documents.
  
In addition, it is very interesting to use a Merkle tree to be able to store within this tree the result of hashing a large number of documents and to store only on the blockchain the value of the root hash of the tree. This has the advantage of considerably reducing the volume of data to be stored on the blockchain as well as the number of transactions to execute to store this data on the blockchain, while guaranteeing the integrity of each document. The use of this Merkle tree data structure used natively in blockchain protocols makes it possible to considerably reduce the cost of electronic signature of a document which will have the effect of extending the use of electronic signature to all types of digital content and use cases associated.
  
Tezos DigiSign is a solution built on top of the Tezos blockchain for reliably timestamping and signing documents in compliance with the regulations, without using a trusted third party.
  

## Benefits of Tezos DigiSign

The Tezos DigiSign solution has the following many advantages.

|Advantage|Comment|
|------------- |-------------
|Independence|Signing documents does not require the use of a trusted third party, SaaS subscription or any software
|Cost savings|The cost of the transaction is the same regardless of the number of documents signed. The unit price to sign a document is several thousand times cheaper than that practiced by the providers offering a solution based on a centralized PKI infrastructure
|Confidentiality|Document does not need to be sent to Tezos DigiSign to be signed, it does not leave the IS and the company network. No need to archive the signed copy of the document with a third party (the signer should just keep proof of signature)
|Multiformat|Any type of file can be signed, there is no limitation on format, weight and size. Network traffic is optimized because documents do not pass through the network
|Transparency|Proof of signature is stored in the blockchain within a Smart Contract accessible without permission, in a public and transparent way. It is easy to prove the authenticity, integrity and timestamp of a document
|Immutability|Once stored in the blockchain, proof of signature cannot be deleted and are accessible to anyone by reading the Smart Contract. Proof of signature remain accessible as long as the blockchain network is active, the retention period is not limited in time

## Multiple use cases

A significant part of the digital documents generated by an organization is not signed solely for economic reasons.
If the cost becomes negligible, the interest in signing the document becomes major.
  
### B2B uses cases

|Domain|Comment|
|------------- |-------------
|Billing|Have a proof in the case of litigation, timestamp to verify a payment date to be respected or to fight fraud  
|Sales|Prove the authenticity of a quote or a commercial proposal, check its validity date  
|Insurance|Facilitate the signing of a subcontract with easy verification of the subcontractor's insurances  
|Loan|Timestamp and signature of the documents required to apply for a business loan (proof of ID and address, business bank statements, financial accounts, VAT returns, …)  
|Human Resources|Timestamp for sensitive mail within the company, which may be used by HR teams  
|Legal|A signed non-disclosure agreement is often just scanned. Timestamping and signing it on blockchain add legal value to it.  
|Diploma and certification|Prove the authenticity of a diploma or professional accreditation (for accountant, certified auditor, doctor, …)  
|Research and development|Generate proof of anteriority on R&D documents that can be used when filing a patent  
|Press and media|Prove a press release is original and fight fake news


### B2C use cases

|Domain|Comment|
|------------- |-------------
|Proof of address|Allow a person to present proof of address certified by the issuing company and whose authenticity can be easily proven  
|Bank statement|Prove to a credit company you have funds at a specific time (e.g. mortgage application).  
|Online purchase certificate|Prove to a repairer that a product is under warranty.  
|Authenticity / ownership certificate|Prove that a good (e.g. a luxury watch) is an original, prove its legitimate possession.  
|Car insurance certificate|Prove to a car dealership that a vehicle is insured without waiting to receive the proof-of-insurance card.  
|Diploma|Prove to a recruiter the authenticity of a diploma obtained  
|Construction permit|Present a document whose authenticity can be verified by all stakeholders  
|Photography|Generate proof of anteriority (timestamped and signed) to defend intellectual property on a photo to be published  


## Tezos Digisign : an open source project  

This project has been developed by a CORE TEAM of skilled collaborators of the SWORD GROUP ([www.Sword-Group.com](http://www.Sword-Group.com)) and Blockchain EZ ([www.Blockchain-ez.com](http://www.Blockchain-ez.com)) companies, with the support of Nomadic Labs ([www.nomadic-labs.com](http://www.nomadic-labs.com)) and the Tezos Foundation ([www.tezos.foundation](http://www.tezos.foundation))  
  
None of these companies are responsible for the usage made by third parties of the Tezos DigiSign project. Sword Group, Blockchain EZ, Nomadic Labs and Tezos Foundation are brands that cannot be used without the written approval of the said organizations.  
  
If you want to be part of this CORE TEAM, please contact us (TEZOSDIGISIGN@sword-group.com).  
  
Tezos DigiSign is an Open Source project complying with the definition given by the Open Source Initiative, as in its 1.9 version ([https://opensource.org/docs/osd](https://opensource.org/docs/osd)). 
  
However, any organization using Tezos DigiSign must:
* Declare its usage to TEZOSDIGISIGN@sword-group.com  
* Accept its name to be communicated as a user of the solution for communication purposes by the core development team
  * If  this is legally complicated for  your organization, details can be discussed : TEZOSDIGISIGN@sword-group.com
* Include in your own software solution / website using Tezos DigiSign and its related documentation a reference to this gitlab page 
  
If you like it, use it and modify it, we would be thankful if you could provide to the community any improvement you make to the solution.  
  
If you need help for implementing the solution, or possibly customizing it, please contact the CORE TEAM (TEZOSDIGISIGN@sword-group.com).  



