import React from 'react';
// import ExpressionEvaluator from './components/ExpressionEvaluator';
import ExpressionForm from './components/ExpressionForm';

function App() {
  const divStyle = {
    paddingLeft: '20px'
  };

  return (
    <div style={divStyle}>
      <h1>Welcome to the Lettuce Wrap!</h1>
      
      Example 1: <ExpressionForm />
      <br />
      {/* Example 2: <ExpressionEvaluator /> */}
    </div>
  );
}

export default App;