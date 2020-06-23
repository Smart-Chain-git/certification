
export interface SignatureCheckRequest {
    documentHash: string,
    proof?: string,
}

export interface Hash {
    hash?: string,
    position: string,
}

export interface Proof {
    file_name: string,
    hash_root: string,
    hash_document: string,
    algorithm: string,
    block_hash?: string,
    blockDepth?: number,
    hash_list: Array<Hash>,
    transaction_hash?: string
    contract_address: string
    signature_date?: Date
    origin_public_key: string,
    public_key: string,
    origin: string,
}

export interface SignatureCheckResponse {
    check_status: number,
    check_process: Array<string>,
    signer?: string,
    timestamp: Date,
    proof: Proof
    error?: string,
    current_depth?: number,
    expected_depth?: number,
    date?: Date,
    public_key?: string
    output: string,
}

export interface AuthRequest {
    user: string,
    password: string,
}

export interface AuthResponse {
    token: string,
}

export interface Account {
    id: string,
    login: string,
    email: string,
    fullName: string | undefined,
    company: string | undefined,
    country: string | undefined,
    publicKey: string | undefined,
    hash: string | undefined,
    isAdmin: boolean,
    disabled: boolean,
}

export interface AccountCreate {
    login: string,
    email: string,
    fullName: string | undefined,
    company: string | undefined,
    country: string | undefined,
    publicKey: string | undefined,
    hash: string | undefined,
    isAdmin: boolean,
    disabled: boolean,
}

export interface AccountPatch {
    email: string | undefined,
    password: string | undefined,
    fullName: string | undefined,
    company: string | undefined,
    country: string | undefined,
    publicKey: string | undefined,
    hash: string | undefined,
    isAdmin: boolean | undefined,
    disabled: boolean | undefined,
}

export interface Job {
    id: string
    createdDate: Date,
    injectedDate?: Date,
    validatedDate?: Date,
    numberOfTry: number,
    blockHash?: string,
    blockDepth?: number,
    algorithm: string,
    flowName: string,
    stateDate: Date,
    state: string,
    contractAddress?: string,
    transactionHash?: string,
    channelName?: string,
    docsNumber: number,
}

export interface JobCriteria {
    id?: string,
    accountId?: string,
    flowName?: string,
    dateBegin?: string,
    dateEnd?: string,
    channel?: string,
    sort?: Array<string>,
    desc?: Array<boolean>,
    page?: number,
    size?: number,
}

export interface Token {
    id: string,
    name: string,
    revoked: boolean,
    creationDate: Date,
    expirationDate?: Date,
}

export interface TokenCreateRequest {
    expirationDate?: Date,
    name: string,
}

export interface SignatureMultiRequest {
    algorithm: string,
    flowName: string,
    callBackUrl: string,
    customFields: {[key: string]: string}
    files: Array<SignatureRequest>
}

export interface SignatureRequest {
    metadata: SignMetaData,
    hash: string,
}

export interface SignMetaData {
    fileName: string,
    fileSize: number,
    customFields?: {[key: string]: string},
}

export interface SignatureResponse {
    jobId: string,
    files: Array<SignMetaData>,
}

export interface SignMetaData {
    fileName: string,
    fileSize: number,
    customFields?: {[key: string]: string},
}


export interface JobFile {
    id: string,
    hash: string,
    jobId: string,
    metadata: SignMetaData,
    proof?: Proof
}

export interface FileCriteria {
    id?: string,
    name?: string,
    hash?: string,
    jobId?: string,
    accountId?: string,
    dateStart?: string,
    dateEnd?: string,
    sort?: Array<string>,
    page?: number,
    size?: number
}

export interface Hash {
    hash?: string,
    position: string
}

export interface Proof {
    file_name: string,
    hash_root: string,
    hash_document: string,
    algorithm: string,
    block_hash?: string,
    blockDepth?: number,
    hash_list: Array<Hash>,
    transaction_hash?: string
    contract_address: string
    signature_date?: Date
    origin_public_key: string,
    public_key: string,
    origin: string
}
