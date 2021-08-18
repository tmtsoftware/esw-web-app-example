// #fetch-imports
import type { RaDecRequest, RaDecResponse } from '../models/Models'
import { get, post } from './Http'
// #fetch-imports

// #fetch-data
export const postRaDecValues = async (
  baseUrl: string,
  raDecRequest: RaDecRequest
): Promise<RaDecResponse | undefined> =>
  (
    await post<RaDecRequest, RaDecResponse>(
      baseUrl + 'raDecValues',
      raDecRequest
    )
  ).parsedBody
// #fetch-data

// #secured-fetch-data
export const securedPostRaDecValues = async (
  baseUrl: string,
  raDecRequest: RaDecRequest,
  token: string
): Promise<RaDecResponse | undefined> =>
  (
    await post<RaDecRequest, RaDecResponse>(
      baseUrl + 'securedRaDecValues',
      raDecRequest,
      {
        Authorization: `Bearer ${token}`
      }
    )
  ).parsedBody
// #secured-fetch-data

// #fetch-saved-ra-dec-values
export const getRaDecValues = async (
  baseUrl: string
): Promise<RaDecResponse[] | undefined> =>
  (await get<RaDecResponse[]>(baseUrl + 'raDecValues')).parsedBody
// #fetch-saved-ra-dec-values
