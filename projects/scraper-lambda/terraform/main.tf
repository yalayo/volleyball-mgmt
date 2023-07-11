# provider config
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.7.0"
    }
  }

  cloud {
    organization = "rondon-sarnik"

    workspaces {
      name = "volleyball-workspace"
    }
  }
}

# Configure the AWS Provider
provider "aws" {
  region = "eu-central-1"
}

# get the policy to be used
data "aws_iam_policy_document" "assume_role" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }

    actions = ["sts:AssumeRole"]
  }
}

# create role to use the lambda service
resource "aws_iam_role" "iam_for_lambda" {
  name               = "iam_for_lambda"
  assume_role_policy = data.aws_iam_policy_document.assume_role.json
}

# create the lambda where to deploy this project on
resource "aws_lambda_function" "target_lambda" {
  # If the file is not in the current working directory you will need to include a
  # path.module in the filename.
  filename      = "function.zip"
  function_name = "scraper_lambda"
  handler       = "executable"
  role          = aws_iam_role.iam_for_lambda.arn

  runtime = "provided"
}