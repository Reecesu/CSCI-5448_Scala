import React from 'react';
import Paper from '@mui/material/Paper';

const Expression = ({ result }) => {
  console.log("HELLO\n\n\n\n" + typeof(result));
  console.log("YO\n\n\n\n" + result);
  console.log("what if...\n\n\n\n");
  
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
      <p>{JSON.stringify(result, null, 2)}</p>
    </Paper>
  );
}

export default Expression;