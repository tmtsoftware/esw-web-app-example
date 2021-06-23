import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import type { HttpLocation } from '@tmtsoftware/esw-ts'
import { HttpConnection, Prefix } from '@tmtsoftware/esw-ts'
import { expect } from 'chai'
import React from 'react'
import { anything, capture, deepEqual, verify, when } from 'ts-mockito'
import { InsertBook } from '../../src/components/InsertBook'
import { locationServiceMock, mockFetch, renderWithLocationService } from '../utils/test-utils'

describe('InsertBook', () => {
  const connection = HttpConnection(Prefix.fromString('ESW.library'), 'Service')

  const httpLocation: HttpLocation = {
    _type: 'HttpLocation',
    uri: '',
    connection,
    metadata: {}
  }
  when(locationServiceMock.find(deepEqual(connection))).thenResolve(httpLocation)

  it('should make a insert request with given book data to the backend', async () => {
    const id = 'some-id'
    const title = 'the conjuring'
    const author = 'Unknown'
    const book = {
      _type: 'InsertBook',
      title,
      author: author
    }

    const assert = (s: boolean) => expect(s).to.false

    const response = new Response(id)
    const fetch = mockFetch()

    when(fetch(anything(), anything())).thenResolve(response)

    renderWithLocationService(<InsertBook reload={true} setReload={assert} />)

    const titleInput = (await screen.findByRole('Title')) as HTMLInputElement
    const authorInput = (await screen.findByRole('AuthorName')) as HTMLInputElement

    userEvent.type(titleInput, title)
    userEvent.type(authorInput, author)

    const submitButton = (await screen.findByRole('Submit')) as HTMLButtonElement

    await waitFor(() => userEvent.click(submitButton))

    verify(locationServiceMock.find(deepEqual(connection))).called()
    const [firstArg, secondArg] = capture(fetch).last()
    expect(firstArg).to.equal(httpLocation.uri + 'insert')

    const expectedReq = {
      method: 'POST',
      body: JSON.stringify(book),
      headers: { 'Content-Type': 'application/json' }
    }

    expect(JSON.stringify(secondArg)).to.equal(JSON.stringify(expectedReq))
  })
})
