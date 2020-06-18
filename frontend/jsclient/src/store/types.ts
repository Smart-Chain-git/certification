
export interface PaginationOption {
    page: number,
    itemsPerPage: number,
    sortBy: Array<string>,
    sortDesc: Array<boolean>,
}

export interface FilterOption {
    flowName: string | undefined,
    id: string | undefined,
    dates: Array<string | undefined>,
    channelName: string | undefined,
}

export interface FileSign {
    name: string,
    size: number,
    hash: string
    keys: Array<string>,
    values: Array<string>,
    newKey?: string,
    newValue?: string,
}
