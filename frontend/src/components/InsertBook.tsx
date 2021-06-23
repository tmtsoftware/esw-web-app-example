import { Button, Form, FormInstance, Input, Typography } from 'antd'
import React, { createRef } from 'react'
import { insertBook, showError } from '../helpers/HttpUtils'
import { useLocationService } from '../helpers/LocationServiceContext'
import type { InsertBookReq } from '../models/Models'
import styles from './style.module.css'
import { resolveBackendUrl } from '../helpers/resolveBackend'

export const InsertBook = ({
  reload,
  setReload
}: {
  reload: boolean
  setReload: (s: boolean) => void
}): JSX.Element => {
  const locationService = useLocationService()
  const ref = createRef<FormInstance>()

  const onFinish = (values: InsertBookReq) => {
    resolveBackendUrl(locationService)
      .then((res) => {
        res && insertBook(res.uri, values).then(() => setReload(!reload))
        ref.current?.resetFields()
      })
      .catch((e) => showError(`Failed to insert the book data for the ${values.title}`, e))
  }

  const layout = {
    labelCol: { span: 8 },
    wrapperCol: { span: 16 }
  }

  return (
    <>
      <Form {...layout} ref={ref} onFinish={onFinish} className={styles.formBody}>
        <Form.Item className={styles.formHeader}>
          <Typography.Title level={4}>{'Add New Book'}</Typography.Title>
        </Form.Item>
        <Form.Item label={'Book Title'} name={'title'} style={{ width: '30rem' }}>
          <Input role={'Title'} />
        </Form.Item>
        <Form.Item label={'Author Name'} name={'author'} style={{ width: '30rem' }}>
          <Input role={'AuthorName'} />
        </Form.Item>
        <Form.Item wrapperCol={{ offset: 8, span: 16 }} className={styles.formFooter}>
          <Button type='primary' htmlType='submit' role={'Submit'}>
            Submit
          </Button>
        </Form.Item>
      </Form>
    </>
  )
}
