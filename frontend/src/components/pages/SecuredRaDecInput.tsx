import { Button, Form, Input } from 'antd'
import React from 'react'
import { useLocationService } from '../../contexts/LocationServiceContext'
import { useAuth } from '../../hooks/useAuth'
import type { RaDecRequest } from '../../models/Models'
import { securedPostRaDecValues } from '../../utils/api'
import { errorMessage } from '../../utils/message'
import { getBackendUrl } from '../../utils/resolveBackend'
// #add-component
// #use-location-service-from-context
export const SecuredRaDecInput = (): JSX.Element => {
  // #add-component
  const locationService = useLocationService()
  const { auth } = useAuth()
  // #use-location-service-from-context

  // #use-fetch
  const onFinish = async (values: RaDecRequest) => {
    const backendUrl = await getBackendUrl(locationService)
    const valueInDecimal = {
      raInDecimals: Number(values.raInDecimals),
      decInDecimals: Number(values.decInDecimals)
    }

    if (backendUrl) {
      const token = auth?.token()
      if (!token) {
        errorMessage('Failed to greet user: Unauthenticated request')
      } else {
        const response = await securedPostRaDecValues(
          backendUrl,
          valueInDecimal,
          token
        )
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
      <Form.Item label='RaInDecimals (secured)' name='raInDecimals'>
        <Input role='RaInDecimals' style={{ marginLeft: '0.5rem' }} />
      </Form.Item>
      <Form.Item label='DecInDecimals (secured)' name='decInDecimals'>
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
