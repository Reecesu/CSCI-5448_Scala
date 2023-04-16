import React, { useState } from 'react';
import UserInput from './components/UserInput';
import Expression from './components/Expression';
import NewExpression from './components/NewExpression';
import Box from '@mui/material/Box';
// import Drawer from './components/Drawer';
import { Divider } from '@mui/material';


export const StoreContext = React.createContext(null);

function App() {
  const [expression, setExpression] = useState(null);
    // SPWI: nextExpression
  const divStyle = {
    paddingRight: '20px'
  };

  return (
    <div style={divStyle}>
      <h1>Welcome to the Lettuce Wrap!</h1>
      
      <UserInput onExpressionChange={setExpression} />
      {/* <Drawer /> */}
      <Divider variant="middle" />
      <br />
      <Box sx={{ display: 'flex', height: '100vh' }}>

        {/*
         <StoreContext.Consumer>
           {expr => <Expression expression={expr} />}
         </StoreContext.Consumer>
         */}
        <Expression expression={expression} />
        <NewExpression />
       </Box>
    </div>
  );
}

export default App;
