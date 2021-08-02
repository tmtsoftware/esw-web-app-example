// #add-test
import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { HttpConnection, HttpLocation, Prefix } from '@tmtsoftware/esw-ts'
import { expect } from 'chai'
import React from 'react'
import { anything, capture, deepEqual, verify, when } from 'ts-mockito'
import { SecuredRaDecInput } from '../../src/components/pages/SecuredRaDecInput'
import {
  locationServiceMock,
  mockFetch,
  renderWithRouter
} from '../utils/test-utils'

describe('Secured Ra Dec Input', () => {
  const connection = HttpConnection(Prefix.fromString('ESW.sample'), 'Service')

  const httpLocation: HttpLocation = {
    _type: 'HttpLocation',
    uri: 'some-backend-url',
    connection,
    metadata: {}
  }
  when(locationServiceMock.find(deepEqual(connection))).thenResolve(
    httpLocation
  )

  it('should render Input form and sent in to backend', async () => {
    const raInDecimals = 2.13
    const decInDecimals = 2.18
    const request = { raInDecimals, decInDecimals }
    const response = new Response(
      JSON.stringify({
        formattedRa: 'some-value1',
        formattedDec: 'some-value2'
      })
    )
    const fetch = mockFetch()

    when(fetch(anything(), anything())).thenResolve(response)

    renderWithRouter(<SecuredRaDecInput />)

    const raInput = (await screen.findByRole(
      'RaInDecimals'
    )) as HTMLInputElement
    const decInput = (await screen.findByRole(
      'DecInDecimals'
    )) as HTMLInputElement

    userEvent.type(raInput, raInDecimals.toString())
    userEvent.type(decInput, decInDecimals.toString())

    const submitButton = (await screen.findByRole(
      'Submit'
    )) as HTMLButtonElement

    await waitFor(() => userEvent.click(submitButton))

    verify(locationServiceMock.find(deepEqual(connection))).called()
    const [firstArg, secondArg] = capture(fetch).last()
    expect(firstArg).to.equal(httpLocation.uri + 'securedRaDecValues')

    const expectedReq = {
      method: 'POST',
      body: JSON.stringify(request),
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer token string'
      }
    }

    expect(JSON.stringify(secondArg)).to.equal(JSON.stringify(expectedReq))
  })
})
// #add-test
