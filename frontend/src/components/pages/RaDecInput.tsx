// #input-imports
import { Button, Form, Input } from 'antd'
import React from 'react'
import { useLocationService } from '../../contexts/LocationServiceContext'
import type { RaDecRequest } from '../../models/Models'
import { postRaDecValues } from '../../utils/api'
import { getBackendUrl } from '../../utils/resolveBackend'
// #input-imports

// #add-component
export const RaDecInput = ({
  reload = true,
  setReload = () => ({})
}: {
  reload?: boolean
  setReload?: (s: boolean) => void
}): React.JSX.Element => {
  // #use-location-service-from-context
  const locationService = useLocationService()
  // #use-location-service-from-context
  // #add-component

  // #use-fetch
  const onFinish = async (values: RaDecRequest) => {
    const backendUrl = await getBackendUrl(locationService)
    const valueInDecimal = {
      raInDecimals: Number(values.raInDecimals),
      decInDecimals: Number(values.decInDecimals)
    }
    if (backendUrl) {
      const response = await postRaDecValues(backendUrl, valueInDecimal)
      setReload(!reload)
      if (response?.formattedRa && response?.formattedDec) {
        console.log(response.formattedRa)
        console.log(response.formattedDec)
      } else {
        console.error(response)
        throw new Error(
          'Invalid response, formattedRa or formattedDec field is missing'
        )
      }
    }
  }
  // #use-fetch
  // #add-component
  return (
    <Form
      onFinish={onFinish}
      style={{ padding: '1rem' }}
      wrapperCol={{
        span: 1
      }}>
      <Form.Item label='RaInDecimals' name='raInDecimals'>
        <Input role='RaInDecimals' style={{ marginLeft: '0.5rem' }} />
      </Form.Item>
      <Form.Item label='DecInDecimals' name='decInDecimals'>
        <Input role='DecInDecimals' />
      </Form.Item>
      <Form.Item>
        <Button type='primary' htmlType='submit' role='Submit'>
          Submit
        </Button>
      </Form.Item>
    </Form>
  )
}
// #add-component
