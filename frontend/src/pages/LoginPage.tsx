import { useState } from 'react'
import { observer } from 'mobx-react-lite'
import { useNavigate } from 'react-router-dom'
import { Card, Tabs, Form, Input, Button, Alert } from 'antd'
import { UserOutlined, LockOutlined, MailOutlined } from '@ant-design/icons'
import { useStore } from '../stores/RootStore'

interface LoginValues {
  username: string
  password: string
}

interface RegisterValues {
  username: string
  email: string
  password: string
  confirmPassword: string
}

const LoginPage = observer(() => {
  const { playerStore } = useStore()
  const navigate = useNavigate()
  const [activeTab, setActiveTab] = useState('login')

  const onLogin = async (values: LoginValues) => {
    try {
      await playerStore.login(values.username, values.password)
      navigate('/')
    } catch {
      // error is set in playerStore
    }
  }

  const onRegister = async (values: RegisterValues) => {
    try {
      await playerStore.register(values.username, values.password, values.email)
      navigate('/')
    } catch {
      // error is set in playerStore
    }
  }

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
      <Card style={{ width: 420 }} title="WallStreet Rookie">
        {playerStore.error && (
          <Alert
            message={playerStore.error}
            type="error"
            showIcon
            closable
            onClose={() => (playerStore.error = null)}
            style={{ marginBottom: 16 }}
          />
        )}

        <Tabs activeKey={activeTab} onChange={setActiveTab} centered items={[
          {
            key: 'login',
            label: 'Login',
            children: (
              <Form onFinish={onLogin} layout="vertical">
                <Form.Item name="username" rules={[{ required: true, message: 'Bitte Username eingeben' }]}>
                  <Input prefix={<UserOutlined />} placeholder="Username" />
                </Form.Item>
                <Form.Item name="password" rules={[{ required: true, message: 'Bitte Passwort eingeben' }]}>
                  <Input.Password prefix={<LockOutlined />} placeholder="Passwort" />
                </Form.Item>
                <Form.Item>
                  <Button type="primary" htmlType="submit" loading={playerStore.loading} block>
                    Login
                  </Button>
                </Form.Item>
              </Form>
            ),
          },
          {
            key: 'register',
            label: 'Registrieren',
            children: (
              <Form onFinish={onRegister} layout="vertical">
                <Form.Item name="username" rules={[
                  { required: true, message: 'Bitte Username eingeben' },
                  { min: 3, message: 'Mindestens 3 Zeichen' },
                ]}>
                  <Input prefix={<UserOutlined />} placeholder="Username" />
                </Form.Item>
                <Form.Item name="email" rules={[
                  { required: true, message: 'Bitte Email eingeben' },
                  { type: 'email', message: 'Ungültige Email-Adresse' },
                ]}>
                  <Input prefix={<MailOutlined />} placeholder="Email" />
                </Form.Item>
                <Form.Item name="password" rules={[
                  { required: true, message: 'Bitte Passwort eingeben' },
                  { min: 6, message: 'Mindestens 6 Zeichen' },
                ]}>
                  <Input.Password prefix={<LockOutlined />} placeholder="Passwort" />
                </Form.Item>
                <Form.Item
                  name="confirmPassword"
                  dependencies={['password']}
                  rules={[
                    { required: true, message: 'Bitte Passwort bestätigen' },
                    ({ getFieldValue }) => ({
                      validator(_, value) {
                        if (!value || getFieldValue('password') === value) {
                          return Promise.resolve()
                        }
                        return Promise.reject(new Error('Passwörter stimmen nicht überein'))
                      },
                    }),
                  ]}
                >
                  <Input.Password prefix={<LockOutlined />} placeholder="Passwort bestätigen" />
                </Form.Item>
                <Form.Item>
                  <Button type="primary" htmlType="submit" loading={playerStore.loading} block>
                    Registrieren
                  </Button>
                </Form.Item>
              </Form>
            ),
          },
        ]} />
      </Card>
    </div>
  )
})

export default LoginPage
