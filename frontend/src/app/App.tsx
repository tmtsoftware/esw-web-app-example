import { Card, Layout, Typography } from 'antd'
import React, { useState } from 'react'
import 'antd/dist/antd.css'
import { BooksTable } from '../components/BooksTable'
import { InsertBook } from '../components/InsertBook'
import { LocationServiceProvider } from '../helpers/LocationServiceContext'
import styles from './app.module.css'

const { Header, Content } = Layout

const App = (): JSX.Element => {
  const [reload, setReload] = useState<boolean>(false)
  return (
    <div className={styles.app}>
      <LocationServiceProvider>
        <Layout>
          <Header className={styles.header}>
            <Typography.Title level={2} style={{ color: 'white' }}>
              {'Library Service'}
            </Typography.Title>
          </Header>
          <Content className={styles.content}>
            <InsertBook reload={reload} setReload={setReload} />
            <Card title={<Typography.Title level={4}>Books In The Library</Typography.Title>}>
              <BooksTable reload={reload} />
            </Card>
          </Content>
        </Layout>
      </LocationServiceProvider>
    </div>
  )
}

export default App
