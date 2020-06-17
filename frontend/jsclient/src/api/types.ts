export interface AccountPatch {
    email: string | undefined
    password: string | null
    fullName: string | undefined
    isAdmin: boolean | undefined
}


export interface SignatureCheckRequest {
    documentHash: string,
    proof?: string,
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


export interface Account {
    id: string
    login: string
    email: string
    fullName: string | undefined
    isAdmin: boolean,
    pubKey: string | null
}

export interface AuthRequest {
    user: string
    password: string
}

export interface AuthResponse {
    token: string
}
