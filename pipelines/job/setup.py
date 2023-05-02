from setuptools import setup, find_packages

with open("requirements.txt") as f:
    requirements = f.readlines()

setup(
    name="Free5gc-filtering",
    version="1.0",
    description="Free5gc Filtering.",
    author="My name",
    author_email="my@email.com",
    packages=find_packages(),
    install_requires=requirements,
)
