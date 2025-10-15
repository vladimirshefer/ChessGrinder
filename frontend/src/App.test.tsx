import React from 'react';
import { render, screen } from '@testing-library/react';
import { vi } from 'vitest';
import App from './App';

// Mock RestApiClient to avoid axios ES6 import issues
vi.mock('lib/api/RestApiClient', () => ({
  default: {}
}));

test('renders chess grinder app', () => {
  render(<App />);
  const titleElement = screen.getByText(/chess grinder/i);
  expect(titleElement).toBeInTheDocument();
});
