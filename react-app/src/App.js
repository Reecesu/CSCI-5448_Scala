import React from 'react';
import ExpressionForm from './components/ExpressionInput';
import ExecutionSteps from './components/ExecutionSteps';
import Evaluation from './components/Evaluation';
import Box from '@mui/material/Box';
import Drawer from './components/Drawer';
import { Divider } from '@mui/material';

function App() {
  const divStyle = {
    paddingRight: '20px'
  };

  return (
    <div style={divStyle}>
      <h1>Welcome to the Lettuce Wrap!</h1>
      
      <ExpressionForm />
      <Drawer />
      <Divider variant="middle" />
      <br />
      <Box sx={{ display: 'flex', height: '100vh' }}>
        <ExecutionSteps />
        <Evaluation />
      </Box>
    </div>
  );
}

export default App;