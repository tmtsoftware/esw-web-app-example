import { screen } from '@testing-library/react'
import type { HttpLocation } from '@tmtsoftware/esw-ts'
import { HttpConnection, Prefix } from '@tmtsoftware/esw-ts'
import React from 'react'
import { anything, deepEqual, verify, when } from 'ts-mockito'
import { BooksTable } from '../../src/components/BooksTable'
import type { Book } from '../../src/models/Models'
import { locationServiceMock, mockFetch, renderWithLocationService } from '../utils/test-utils'

describe('BooksTable', () => {
  const connection = HttpConnection(Prefix.fromString('ESW.library'), 'Service')

  const httpLocation: HttpLocation = {
    _type: 'HttpLocation',
    uri: '',
    connection,
    metadata: {}
  }
  when(locationServiceMock.find(deepEqual(connection))).thenResolve(httpLocation)

  it('should render a table filled with data related to all the books', async () => {
    const fetch = mockFetch()
    const book: Book = {
      id: '',
      title: 'the conjuring',
      author: 'Unknown',
      available: false
    }

    const response = new Response(JSON.stringify([book]))
    when(fetch(anything())).thenResolve(response)

    renderWithLocationService(<BooksTable reload={true} />)

    await screen.findByRole('table')
    await screen.findByRole('row', { name: 'the conjuring Unknown No' })

    verify(locationServiceMock.find(deepEqual(connection))).called()
    verify(fetch(httpLocation.uri + 'books')).called()
  })
})
