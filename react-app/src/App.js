import React from 'react';
import ExpressionInput from './components/ExpressionInput';
import Expression from './components/Expression';
import NewExpression from './components/NewExpression';
import Box from '@mui/material/Box';
// import Drawer from './components/Drawer';
import { Divider } from '@mui/material';


export const StoreContext = React.createContext(null);

function App() {
  const divStyle = {
    paddingRight: '20px'
  };

  return (
    <div style={divStyle}>
      <h1>Welcome to the Lettuce Wrap!</h1>
      
      <ExpressionInput />
      {/* <Drawer /> */}
      <Divider variant="middle" />
      <br />
      <Box sx={{ display: 'flex', height: '100vh' }}>

        {/*
         <StoreContext.Consumer>
           {expr => <Expression expression={expr} />}
         </StoreContext.Consumer>
         */}
         <Expression />

         <NewExpression />
       </Box>
    </div>
  );
}

export default App;
