export interface Book {
  id: string
  title: string
  author: string
  available: boolean
}

export interface InsertBookReq {
  author: string
  title: string
}
