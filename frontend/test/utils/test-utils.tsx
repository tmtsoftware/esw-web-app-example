import { render, RenderResult } from '@testing-library/react'
import type { LocationService } from '@tmtsoftware/esw-ts'
import React from 'react'
import { instance, mock } from 'ts-mockito'
import { LocationServiceProvider } from '../../src/helpers/LocationServiceContext'

export const locationServiceMock = mock<LocationService>()

class MockedFetch {
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  // eslint-disable-next-line @typescript-eslint/no-empty-function
  fetch(input: RequestInfo, init?: RequestInit): Promise<Response> {}
}

export const mockFetch = (): typeof window.fetch => {
  const mockedFetch = mock(MockedFetch)
  window.fetch = instance(mockedFetch).fetch
  return mockedFetch.fetch
}

export const renderWithLocationService = (ui: React.ReactElement): RenderResult => {
  const provider = ({ children }: { children: React.ReactNode }) => (
    <LocationServiceProvider initialValue={instance(locationServiceMock)}>{children}</LocationServiceProvider>
  )
  return render(ui, {
    wrapper: provider as React.FunctionComponent
  })
}
