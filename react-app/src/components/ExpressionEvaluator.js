import React, { useState } from 'react';
import axios from 'axios';

const ExpressionEvaluator = () => {
  const [expression, setExpression] = useState('');

  const evaluateExpression = async (expression) => {
    try {
      const response = await axios.post(`${process.env.REACT_APP_BACKEND_URL}/evaluate`, {
        expression: expression,
      });

      console.log(response.data);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    evaluateExpression(expression);
  };

  return (
    <div>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          value={expression}
          onChange={(event) => setExpression(event.target.value)}
          placeholder="Enter expression"
        />
        <button type="submit">Evaluate</button>
      </form>
    </div>
  );
};

export default ExpressionEvaluator;
