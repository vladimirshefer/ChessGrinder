import React from 'react';
import {Link, useLocation} from "react-router-dom";

interface ErrorBoundaryProps {
    children: React.ReactNode;
}

interface ErrorBoundaryState {
    hasError: boolean;
    error?: Error;
    errorInfo?: React.ErrorInfo;
}

class ErrorBoundary extends React.Component<ErrorBoundaryProps, ErrorBoundaryState> {
    constructor(props: ErrorBoundaryProps) {
        super(props);
        this.state = {hasError: false};
    }

    static getDerivedStateFromError(error: Error): Partial<ErrorBoundaryState> {
        return {hasError: true, error};
    }

    componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
        console.error("RouteErrorBoundary caught an error", error, errorInfo);
        this.setState({errorInfo});
    }

    render() {
        if (this.state.hasError) {
            return <RouteErrorFallback error={this.state.error} errorInfo={this.state.errorInfo}/>;
        }
        return this.props.children;
    }
}

function RouteErrorFallback({error, errorInfo}: { error?: Error, errorInfo?: React.ErrorInfo }) {
    const stack = [error?.stack, errorInfo?.componentStack].filter(Boolean).join("\n\n");
    return <div className={"grid w-full p-5 gap-3 justify-center"}>
        <h1 className={"font-bold"}>Something went wrong.</h1>
        <Link className={"underline"} to={"/"}>Return to the Main Page</Link>
        <h1 className={"font-bold"}>{error?.message ?? "Unknown error"}</h1>
        {stack &&
            <details className={"whitespace-pre-wrap text-sm text-left"}>
                <summary className={"cursor-pointer"}>Stacktrace</summary>
                <pre className={"whitespace-pre-wrap"}>{stack}</pre>
            </details>
        }
    </div>
}

/**
 * Error boundary meant to be placed around routed content (e.g. an Outlet or a Route's element).
 * It is keyed by the current pathname, so navigating to another route resets the boundary
 * and does not leave the whole application stuck on a stale error screen.
 */
export default function RouteErrorBoundary({children}: ErrorBoundaryProps) {
    const location = useLocation();
    return <ErrorBoundary key={location.pathname}>{children}</ErrorBoundary>;
}
