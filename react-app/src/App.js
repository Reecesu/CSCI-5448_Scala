import React, { useState } from 'react';
import UserInput from './components/UserInput';
import Expression from './components/Expression';
import NewExpression from './components/NewExpression';
import Box from '@mui/material/Box';
import Drawer from './components/Drawer';
import { Divider } from '@mui/material';


export const StoreContext = React.createContext(null);

function App() {
  const [expression, setExpression] = useState(undefined);
  const [newExpression, setNewExpression] = useState(undefined);

    // SPWI: nextExpression
  const divStyle = {
    paddingRight: '20px'
  };

  return (
    <div style={divStyle}>
      <h1>Welcome to the Lettuce Wrap!</h1>
      
      <UserInput onExpressionChange={setExpression} onNewExpressionChange={setNewExpression}/>
      <Drawer />
      <Divider variant="middle" />
      <br />
      <Box sx={{ display: 'flex', height: '100vh' }}>
        <Expression expression={expression} />
        <NewExpression expression={newExpression}/>
       </Box>
    </div>
  );
}

export default App;
