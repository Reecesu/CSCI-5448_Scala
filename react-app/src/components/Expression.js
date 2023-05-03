import React from 'react';
import Paper from '@mui/material/Paper';

/**
 * The bottom left hand user expression panel
 * 
 * references: https://mui.com/material-ui/react-paper/
 * 
 * @param {*} param0 
 * @returns 
 */

const Expression = ({ expression }) => {
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

export default Expression;
