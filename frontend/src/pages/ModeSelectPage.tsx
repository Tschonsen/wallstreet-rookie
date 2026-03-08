import { observer } from 'mobx-react-lite'
import { Card, Col, Row, Typography, App } from 'antd'
import { UserOutlined, TeamOutlined, ThunderboltOutlined, GlobalOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { useStore } from '../stores/RootStore'

const { Title, Text, Paragraph } = Typography

const ModeSelectPage = observer(() => {
  const { gameStore } = useStore()
  const { message } = App.useApp()
  const navigate = useNavigate()

  const startSingleplayer = async () => {
    try {
      await gameStore.startSingleplayer()
      navigate('/dashboard')
    } catch {
      message.error('Fehler beim Starten des Singleplayer-Modus')
    }
  }

  const startMultiplayer = async () => {
    try {
      await gameStore.joinMultiplayer()
      navigate('/dashboard')
    } catch {
      message.error('Fehler beim Beitreten des Multiplayer-Modus')
    }
  }

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', padding: 24 }}>
      <div style={{ maxWidth: 800, width: '100%' }}>
        <Title level={2} style={{ textAlign: 'center', marginBottom: 40 }}>Spielmodus wählen</Title>

        <Row gutter={24}>
          <Col span={12}>
            <Card
              hoverable
              onClick={startSingleplayer}
              style={{ textAlign: 'center', height: '100%' }}
              styles={{ body: { padding: 32 } }}
            >
              <UserOutlined style={{ fontSize: 48, color: '#1890ff', marginBottom: 16 }} />
              <Title level={3}>Singleplayer</Title>
              <Paragraph type="secondary">
                <ThunderboltOutlined /> Eigener Markt
              </Paragraph>
              <Paragraph type="secondary">
                <ThunderboltOutlined /> Zeit vorspulen
              </Paragraph>
              <Paragraph type="secondary">
                <ThunderboltOutlined /> In deinem Tempo
              </Paragraph>
              <Text type="secondary" style={{ marginTop: 16, display: 'block' }}>
                Starte mit $100.000 und trade in deinem eigenen Tempo.
              </Text>
            </Card>
          </Col>
          <Col span={12}>
            <Card
              hoverable
              onClick={startMultiplayer}
              style={{ textAlign: 'center', height: '100%' }}
              styles={{ body: { padding: 32 } }}
            >
              <TeamOutlined style={{ fontSize: 48, color: '#52c41a', marginBottom: 16 }} />
              <Title level={3}>Multiplayer</Title>
              <Paragraph type="secondary">
                <GlobalOutlined /> Echtzeit-Kurse
              </Paragraph>
              <Paragraph type="secondary">
                <GlobalOutlined /> Globale Rangliste
              </Paragraph>
              <Paragraph type="secondary">
                <GlobalOutlined /> Gegen andere Spieler
              </Paragraph>
              <Text type="secondary" style={{ marginTop: 16, display: 'block' }}>
                Tritt gegen andere Spieler an und zeige dein Können.
              </Text>
            </Card>
          </Col>
        </Row>
      </div>
    </div>
  )
})

export default ModeSelectPage
