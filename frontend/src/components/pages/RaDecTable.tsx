// #table-imports
import { Table, Typography } from 'antd'
import type { ColumnsType } from 'antd/lib/table'
import React, { useEffect, useState } from 'react'
import { useLocationService } from '../../contexts/LocationServiceContext'
import type { RaDecResponse } from '../../models/Models'
import { getRaDecValues } from '../../utils/api'
import { errorMessage } from '../../utils/message'
import { getBackendUrl } from '../../utils/resolveBackend'
// #table-imports

//  #add-columns
const HeaderTitle = ({ title }: { title: string }): JSX.Element => (
  <Typography.Title level={5} style={{ marginBottom: 0 }}>
    {title}
  </Typography.Title>
)

const columns: ColumnsType<RaDecResponse> = [
  {
    title: <HeaderTitle title={'Formatted Ra Value'} />,
    dataIndex: 'formattedRa',
    key: 'formattedRa'
  },
  {
    title: <HeaderTitle title={'Formatted Dec Value'} />,
    dataIndex: 'formattedDec',
    key: 'formattedDec'
  }
]
// #add-columns

// #use-fetch
// #add-component
export const RaDecTable = ({ reload }: { reload: boolean }): JSX.Element => {
  // #add-component
  const locationService = useLocationService()
  const [raDecValues, setRaValues] = useState<RaDecResponse[]>()

  useEffect(() => {
    async function fetchRaValues() {
      const backendUrl = await getBackendUrl(locationService)
      if (backendUrl) {
        const raDecValues = await getRaDecValues(backendUrl)
        console.log('raDecValues', raDecValues)
        setRaValues(raDecValues)
      } else {
        errorMessage('Failed to fetch ra values')
      }
    }

    fetchRaValues()
  }, [reload, locationService])

  // #add-component
  return (
    <Table
      rowKey={(record) => record.id}
      pagination={false}
      dataSource={raDecValues}
      columns={columns}
      bordered
    />
  )
}
// #add-component
// #use-fetch
