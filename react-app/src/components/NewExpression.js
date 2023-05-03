import React from 'react';
import Paper from '@mui/material/Paper';


/**
 * The bottom right hand user expression panel
 * 
 * references: https://mui.com/material-ui/react-paper/
 * 
 * TODO: EVSU Remove string quotations
 * 
 * @param {*} param0 
 * @returns 
 */
const NewExpression = ({ expression }) => {
  return (
    <Paper
      sx={{
        width: '50%',
        height: '100%',
        overflow: 'auto',
        borderWidth: 1,
        borderStyle: 'solid',
        mx: 1,
      }}
      variant="outlined"
    >
      <p>{JSON.stringify(expression, null, 2)}</p>
    </Paper>
  );
}

export default NewExpression;