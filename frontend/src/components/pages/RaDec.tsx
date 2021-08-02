import React from 'react'
import { RaDecInput as RaDecInput } from './RaDecInput'
import { RaDecTable as RaDecTable } from './RaDecTable'

// #add-component
export const RaDec = (): JSX.Element => {
  return (
    <>
      <RaDecInput />
      <RaDecTable />
    </>
  )
}
// #add-component
