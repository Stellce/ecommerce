# Common DTOs

- PageResponse<T>
  - content: List<T>
  - empty: boolean
  - first: boolean
  - last: boolean
  - pageNumber: number
  - size: number
  - totalPages: number
  - totalElements: number

- ErrorResponse
  - timestamp: datetime
  - status: number
  - error: string
  - message: string
  - path: string