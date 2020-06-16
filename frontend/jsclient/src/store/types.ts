export interface PaginationOption {
    page: number,
    itemsPerPage: number,
    sortBy: Array<string>,
    sortDesc: Array<boolean>
}

export interface FilterOption {
    flowName: string | undefined,
    id: string | undefined,
    dates: Array<string | undefined>,
    channelName: string | undefined
}
