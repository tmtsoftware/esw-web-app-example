import React, { useState } from 'react'
import { RaDecInput as RaDecInput } from './RaDecInput'
import { RaDecTable as RaDecTable } from './RaDecTable'

// #add-component
export const RaDec = (): React.JSX.Element => {
  const [reload, setReload] = useState<boolean>(false)
  return (
    <>
      <RaDecInput reload={reload} setReload={setReload} />
      <RaDecTable reload={reload} />
    </>
  )
}
// #add-component
