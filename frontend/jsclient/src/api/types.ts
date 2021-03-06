export interface SignatureCheckRequest {
    documentHash: string,
    proof?: File,
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
    urls?: Array<URLNode>
}

export interface SignatureCheckResponse {
    check_status?: number,
    check_process?: Array<string>,
    signer?: string,
    timestamp?: Date,
    proof?: Proof
    error?: string,
    current_depth?: number,
    expected_depth?: number,
    current_age?: string,
    expected_age?: string,
    date?: Date,
    public_key?: string
    output: string,
    hash_document_proof?: string,
    hash_document?: string,
    hash_root?: string,
    origin_public_key?: string,
    proof_file_origin_public_key?: string,
    hash?: string,
    proof_file_hash?: string,
    signature_date?: Date,
    proof_file_signature_date?: Date,
    proof_file_algorithm?: string,
    proof_file_public_key?: string,
    contract_address?: string,
    proof_file_contract_address?: string,
}

export interface URLNode {
    url: string,
    type: string,
    comment: string
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
    firstLogin: boolean,
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
    callBackStatus: string,
    contractAddress?: string,
    transactionHash?: string,
    channelName?: string,
    docsNumber: number,
    rootHash?: string,
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
    jwtToken: string
}

export interface TokenCreateRequest {
    expirationDate?: Date,
    name: string,
}

export interface SignatureMultiRequest {
    algorithm: string,
    flowName: string,
    callBackUrl?: string,
    customFields: { [key: string]: string }
    files: Array<SignatureRequest>
}


export interface SignatureRequest {
    metadata: SignMetaData,
    hash: string,
}

export interface SignMetaData {
    fileName: string,
    fileSize: number,
    customFields?: { [key: string]: string },
}

export interface SignatureResponse {
    jobId: string,
    files: Array<SignMetaData>,
}

export interface AccountValidation {
    password: string
}

export interface SignMetaData {
    fileName: string,
    fileSize: number,
    customFields?: { [key: string]: string },
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


export interface DashBoardStat {
    jobsCreatedCount: number,
    jobsProcessedCount: number,
    documentsSignedCount: number
}


export  interface MerkelTree {
    algorithm: string,
    root: TreeNode,
}

export  interface TreeNode {
    hash: string,
    left?: Node,
    right?: Node,
}

