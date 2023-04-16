import React from 'react';
import Paper from '@mui/material/Paper';

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
      {/* TODO: EVSU Remove string quotations  */}
    </Paper>
  );
}

export default Expression;
